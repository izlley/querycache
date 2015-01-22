function formatDate(d) {
    return "%04d-%02d-%02d %02d:%02d:%02d".sprintf(
        d.getFullYear(),
        d.getMonth() + 1,
        d.getDate(),
        d.getHours(),
        d.getMinutes(),
        d.getSeconds()
    );
}