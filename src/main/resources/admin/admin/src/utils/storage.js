const storage = {
    set(key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    },
    get(key) {
        const item = localStorage.getItem(key);
        if (!item) return "";

        try {
            // 尝试解析JSON，如果成功则返回解析后的值
            return JSON.parse(item);
        } catch (e) {
            // 如果解析失败，直接返回原始字符串
            return item;
        }
    },
    getObj(key) {
        return localStorage.getItem(key) ? JSON.parse(localStorage.getItem(key)) : null;
    },
    remove(key) {
        localStorage.removeItem(key);
    },
    clear() {
        localStorage.clear();
    }
}
export default storage;