![](docs/images/menu_logo_wecross.svg)

# WeCross控制台
[![CodeFactor](https://www.codefactor.io/repository/github/webankfintech/WeCross-Console/badge)](https://www.codefactor.io/repository/github/webankfintech/WeCross--Console) [![Build Status](https://travis-ci.org/WeBankFinTech/WeCross-Console.svg?branch=dev)](https://travis-ci.org/WeBankFinTech/WeCross-Console) [![Latest release](https://img.shields.io/github/release/WeBankFinTech/WeCross-Console.svg)](https://github.com/WeBankFinTech/WeCross-Console/releases/latest)
![](https://img.shields.io/github/license/WeBankFinTech/WeCross) 

WeCross控制台是[WeCross](https://github.com/WeBankFinTech/WeCross)的重要交互式客户端工具。

## 关键特性

- 支持交互式命令
- 提供了针对跨链资源的操作命令

## 部署使用

* 可以直接下载WeCross控制台压缩包，然后解压并使用WeCross控制台。具体参考[WeCross控制台部署和使用文档](https://wecross.readthedocs.io/zh_CN/latest/docs/tutorial/setup.html#id5)

## 源码编译

**环境要求**:

  - [JDK8及以上](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/sdk.html#id1)
  - Gradle 5.0及以上

**编译运行如下命令**:

```shell
$ cd WeCross-Console
$ ./gradlew assemble
```
如果编译成功，将在当前目录生成一个dist目录。

## 贡献说明

欢迎参与WeCross社区的维护和建设：

- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目左上方Star按钮)。
- 提交代码(Pull requests)，参考我们的[代码贡献流程](CONTRIBUTING_CN.md)。
- [提问和提交BUG](https://github.com/WeBankFinTech/WeCross-Console/issues/new)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

希望在您的参与下，WeCross会越来越好！

## 社区
联系我们：wecross@webank.com

## License

![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

Web3SDK的开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](./LICENSE)。
