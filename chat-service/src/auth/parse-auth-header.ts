
export function parseAuthHeader(header: string) {
  return JSON.parse(Buffer.from(header, 'base64').toString());
}
