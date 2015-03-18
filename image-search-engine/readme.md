# 构建图像搜索引擎查找相册中的图像

使用颜色直方图对相册中的图像的颜色部分进行分类。接着，使用颜色描述符索引化相册，提取相册中每一副图像的颜色直方图。

使用卡方距离比较图像，这是比较离散概率分布最常见的选择。

提交待搜索图像并返回查找结果:

```bash
python search.py --index index.csv --query queries/108100.png --result-path dataset
```
