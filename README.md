![](docs/images/menu_logo_wecross.svg)

# WeCross-Console

[![CodeFactor](https://www.codefactor.io/repository/github/webankblockchain/WeCross-Console/badge)](https://www.codefactor.io/repository/github/webankblockchain/WeCross-Console) [![Build Status](https://travis-ci.org/WeBankFinTech/WeCross-Console.svg?branch=dev)](https://travis-ci.org/WeBankFinTech/WeCross-Console) [![Latest release](https://img.shields.io/github/release/WeBankFinTech/WeCross-Console.svg)](https://github.com/WeBankFinTech/WeCross-Console/releases/latest)
[![License](https://img.shields.io/github/license/WeBankFinTech/WeCross-Console)](https://www.apache.org/licenses/LICENSE-2.0) [![Language](https://img.shields.io/badge/Language-Java-blue.svg)](https://www.java.com)

WeCross Console是[WeCross](https://github.com/WeBankFinTech/WeCross)的重要交互式客户端工具。

## 关键特性

- 支持交互式命令
- 提供了针对跨链资源的操作命令

## 部署使用

* 可直接下载WeCross控制台压缩包，然后解压并使用。具体请参考[部署和使用文档](https://wecross.readthedocs.io/zh_CN/latest/docs/tutorial/networks.html#id11)

## 源码编译

**环境要求**:

  - [JDK8及以上](https://www.oracle.com/java/technologies/javase-downloads.html)
  - Gradle 5.0及以上

**编译命令**:

```bash
$ cd WeCross-Console
$ ./gradlew assemble
```

如果编译成功，将在当前目录的dist/apps目录下生成控制台jar包。

## 贡献说明

欢迎参与WeCross社区的维护和建设：

- 提交代码(Pull requests)，可参考[代码贡献流程](CONTRIBUTING.md)以及[wiki指南](https://github.com/WeBankFinTech/WeCross/wiki/%E8%B4%A1%E7%8C%AE%E4%BB%A3%E7%A0%81)
- [提问和提交BUG](https://github.com/WeBankFinTech/WeCross-Console/issues/new)

希望在您的参与下，WeCross会越来越好！

## 社区
联系我们：wecross@webank.com

## License

![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

WeCross Console的开源协议为[Apache License 2.0](http://www.apache.org/licenses/)，详情参考[LICENSE](./LICENSE)。
