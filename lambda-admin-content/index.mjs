import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, GetCommand, PutCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { PutObjectCommand, S3Client } from "@aws-sdk/client-s3";
import { getSignedUrl } from "@aws-sdk/s3-request-presigner";
import { randomUUID } from "node:crypto";

const db = DynamoDBDocumentClient.from(new DynamoDBClient({}));
const s3 = new S3Client({});
const tableName = process.env.TABLE_NAME || "peec-site-content";
const allowedOrigin = process.env.ALLOWED_ORIGIN || "https://www.peechurch.org";
const mediaBucket = process.env.MEDIA_BUCKET || "peec-frontend-sermon";
const mediaPublicBase = process.env.MEDIA_PUBLIC_BASE || "https://www.peechurch.org/admin-media";
const allowedSections = new Set(["announcements", "events", "giving", "pictures"]);
const editorSections = new Set(["announcements", "events"]);

const groupsFromClaims = (claims) => {
  const value = claims?.["cognito:groups"];
  if (Array.isArray(value)) return new Set(value);
  if (!value) return new Set();
  try {
    const parsed = JSON.parse(value);
    if (Array.isArray(parsed)) return new Set(parsed);
  } catch {
    // API Gateway may return a bracketed or comma-separated claim.
  }
  return new Set(String(value).replace(/[\[\]"]/g, "").split(/[\s,]+/).filter(Boolean));
};

const mayEditSection = (claims, section) => {
  const groups = groupsFromClaims(claims);
  return groups.has("SuperAdmin") || (groups.has("ContentEditor") && editorSections.has(section));
};

const isSuperAdmin = (claims) => groupsFromClaims(claims).has("SuperAdmin");

const response = (statusCode, body, origin = allowedOrigin) => ({
  statusCode,
  headers: {
    "content-type": "application/json",
    "access-control-allow-origin": origin,
    "access-control-allow-headers": "authorization,content-type",
    "access-control-allow-methods": "GET,POST,PUT,OPTIONS",
    "cache-control": "no-store",
  },
  body: body === undefined ? "" : JSON.stringify(body),
});

const sectionFromEvent = (event) => {
  const routeSection = event?.pathParameters?.section;
  if (routeSection) return routeSection.toLowerCase();
  const parts = (event?.rawPath || "").split("/").filter(Boolean);
  return parts[0] === "content" ? parts[1]?.toLowerCase() : undefined;
};

export const handler = async (event) => {
  const method = event?.requestContext?.http?.method || event?.httpMethod || "GET";
  const origin = event?.headers?.origin;
  const corsOrigin = origin === "https://peechurch.org" || origin === allowedOrigin ? origin : allowedOrigin;
  if (method === "OPTIONS") return response(204, undefined, corsOrigin);

  try {
    const section = sectionFromEvent(event);
    if (method === "GET" && section) {
      if (!allowedSections.has(section)) return response(404, { error: "Unknown section" }, corsOrigin);
      const result = await db.send(new GetCommand({ TableName: tableName, Key: { section } }));
      return response(200, result.Item || { section, content: null }, corsOrigin);
    }
    if (method === "GET") {
      const result = await db.send(new ScanCommand({ TableName: tableName }));
      const content = Object.fromEntries((result.Items || []).map((item) => [item.section, item]));
      return response(200, { content }, corsOrigin);
    }
    if (method === "PUT" && section) {
      const claims = event?.requestContext?.authorizer?.jwt?.claims;
      if (!claims?.sub) return response(401, { error: "Administrator login required" }, corsOrigin);
      if (!allowedSections.has(section)) return response(400, { error: "Unknown section" }, corsOrigin);
      if (!mayEditSection(claims, section)) return response(403, { error: "You do not have permission to change this section" }, corsOrigin);
      const body = JSON.parse(event.body || "{}");
      if (JSON.stringify(body).length > 300000) return response(413, { error: "Content is too large" }, corsOrigin);
      const item = { section, content: body.content ?? body, updatedAt: new Date().toISOString(), updatedBy: claims.email || claims.sub };
      await db.send(new PutCommand({ TableName: tableName, Item: item }));
      return response(200, item, corsOrigin);
    }
    if (method === "POST" && event?.rawPath === "/media/upload") {
      const claims = event?.requestContext?.authorizer?.jwt?.claims;
      if (!claims?.sub) return response(401, { error: "Administrator login required" }, corsOrigin);
      if (!isSuperAdmin(claims)) return response(403, { error: "Only a SuperAdmin may upload website pictures" }, corsOrigin);
      const body = JSON.parse(event.body || "{}");
      const contentType = String(body.contentType || "").toLowerCase();
      const allowedTypes = new Map([["image/jpeg", "jpg"], ["image/png", "png"], ["image/webp", "webp"]]);
      const extension = allowedTypes.get(contentType);
      if (!extension) return response(400, { error: "Only JPG, PNG, and WebP images are allowed" }, corsOrigin);
      const key = `admin-media/${randomUUID()}.${extension}`;
      const command = new PutObjectCommand({ Bucket: mediaBucket, Key: key, ContentType: contentType, CacheControl: "public,max-age=31536000,immutable" });
      const uploadUrl = await getSignedUrl(s3, command, { expiresIn: 300 });
      return response(200, { uploadUrl, publicUrl: `${mediaPublicBase}/${key.split("/").pop()}`, key }, corsOrigin);
    }
    return response(405, { error: "Method not allowed" }, corsOrigin);
  } catch (error) {
    console.error("PEEC content API error", error);
    return response(500, { error: "Unable to process the request" }, corsOrigin);
  }
};
