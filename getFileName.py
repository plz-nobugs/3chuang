import os

def list_files(startpath='.', excluded_extensions=None):
    # 如果没有提供排除列表，则初始化为空列表
    if excluded_extensions is None:
        excluded_extensions = []
    
    # 将所有排除的扩展名转为小写，以进行不区分大小写的比较
    excluded_extensions = [ext.lower() for ext in excluded_extensions]
    
    for root, dirs, files in os.walk(startpath):
        for file in files:
            # 获取文件扩展名
            _, extension = os.path.splitext(file)
            extension = extension.lower()  # 转换为小写进行比较
            
            # 如果文件扩展名不在排除列表中，则打印该文件
            if extension not in excluded_extensions:
                # 获取文件的完整路径
                full_path = os.path.join(root, file)
                
                # 获取相对于起始路径的相对路径
                rel_path = os.path.relpath(full_path, startpath)
                
                # 将正斜杠转换为反斜杠，以匹配Windows风格的路径
                rel_path = rel_path.replace('/', '\\')
                
                # 为所有不以.\开头的路径添加.\前缀
                if not rel_path.startswith('.\\'):
                    rel_path = '.\\' + rel_path
                
                print(rel_path)

if __name__ == "__main__":
    # 示例：排除.pyc、.git、.jpg文件
    excluded_exts = ['.py', '.svg', '.jpg']
    list_files(excluded_extensions=excluded_exts)
