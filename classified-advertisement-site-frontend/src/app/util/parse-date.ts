
export function parseDate(date?: string): Date | undefined {
  if (!date) {
    return undefined;
  }

  return new Date(date);
}
