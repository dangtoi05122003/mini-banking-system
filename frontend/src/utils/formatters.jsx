function formatMoney(value, sign = false) {
    const amount = Number(value) || 0;
    const formatted = Math.abs(amount).toLocaleString("vi-VN", {maximumFractionDigits: 0});
    if (!sign) {
        return `${formatted} ₫`;
    }
    return amount >= 0 ? `+${formatted} ₫` : `-${formatted} ₫`;
}
export {formatMoney}