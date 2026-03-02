export function datetimeLocalToIso(value) {
    // "YYYY-MM-DDTHH:mm"
    if (!value) return "";

    const [datePart, timePart] = value.split("T");
    if (!datePart || !timePart) return "";

    const [y, m, d] = datePart.split("-").map(Number);
    const [hh, mm] = timePart.split(":").map(Number);

    const dt = new Date(y, (m ?? 1) - 1, d ?? 1, hh ?? 0, mm ?? 0, 0, 0);
    return Number.isNaN(dt.getTime()) ? "" : dt.toISOString();
}