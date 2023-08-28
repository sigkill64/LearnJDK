# JDK 源码阅读笔记

## 项目结构

- `src/`：存放由 JDK 中的 src.zip 解包而来的源码源码文件
- `test/`：存放测试文件
- `ReadTypes.md`：按功能划分的代码文件清单，阅读进度也记录在此文件中
- `ReadFiles.md`：记录已阅源文件
- `Test.md`：记录测试功能及对应测试代码文件

## Commit 图例

| 序号  |       emoji        |                           在本项目中的含义           |       简写标记       |
| :---: | :----------------: | :--------------------------------------------------- | :------------------: |
|  (0)  |       :tada:       | 初始化项目                                           |       `:tada:`       |
|  (1)  |       :memo:       | 更新文档，包括但不限于README                         |       `:memo:`       |
|  (2)  |       :bulb:       | 发布新的阅读笔记                                     |       `:bulb:`       |
|  (3)  |     :sparkles:     | 增量更新阅读笔记                                     |     `:sparkles:`     |
|  (4)  |     :recycle:      | 重构，主要指修改已有的阅读笔记，极少情形下会修改源码 |     `:recycle:`      |
|  (5)  |     :pencil2:      | 校对，主要指更正错别字、调整源码分组、修改源码排版等 |     `:pencil2:`      |
|  (6)  | :white_check_mark: | 发布测试文件                                         | `:white_check_mark:` |

## 修改说明

利用包含而不限于以下方法增强代码可读性，便于插入笔记：

- 修改无意义的变量名为更易懂的变量名
- 重构控制语句结构
- for 和 foreach 的转换
- 拆分过长且难读的调用链，将中间过程单独摘出来
- 提取频繁出现的某段代码为单个方法
- 将一个文件内的多个顶级类拆分到不同的文件中
- 匿名类、非匿名类、函数表达式的转换
- 函数式调用与普通调用的转换


## 相关链接

- [构建 JDK](https://hg.openjdk.java.net/jdk/jdk11/raw-file/tip/doc/building.html)
- [Full Emoji List](https://unicode.org/emoji/charts/full-emoji-list.html)
- [gitmoji](https://gitmoji.carloscuesta.me/)
