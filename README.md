# 教授面对面
### 1.部署方式
前置步骤: 安装docker和docker-compose,maven3.6.3
开始部署
- clone 项目到本地
- cd education 进入项目文件夹
- 编译项目
    ```bash
    mvn clean package -DskipTests
    ```
- 修改docker-compose当中的mail相关信息
- 运行docker-compose
  ```bash
  docker-compose up -d
  ```
- 打开localhost:15672，进入rabbitmq管理界面然后新建2个队列
  - mail.email_check
  - msg.text_msg_check
- 备份es数据
进入es容器的控制台安装ik分词器
  ```bash
    /bin/echo -e "y" | elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.12.1/elasticsearch-analysis-ik-7.12.1.zip
  ```
  重启es容器
安装es数据备份工具
   ```bash
    npm install elasticdump -g
   ```
   然后执行如下命令
   ```bash
    elasticdump  --output http://localhost:9200/qa --input=./container-data/es/es_backup/qa_analyzer.json --type=analyzer
    elasticdump  --output http://localhost:9200/professor_info --input=./container-data/es/es_backup/professor_info_analyzer.json --type=analyzer

    elasticdump  --output http://localhost:9200/professor_info --input=./container-data/es/es_backup/professor_info_mapping.json --type=mapping
    elasticdump  --output http://localhost:9200/qa --input=./container-data/es/es_backup/qa_mapping.json --type=mapping

    elasticdump  --output http://localhost:9200/qa --input=./container-data/es/es_backup/qa_data.json --type=data
    elasticdump  --output http://localhost:9200/professor_info --input=./container-data/es/es_backup/professor_info_data.json --type=data
   ```
备份minio_data
  下载data文件夹: https://wwi.lanzoup.com/ioM8f0q786ib
  解压到当前目录下的container-data/minio/data 如果当前不存在就新建
  重新部署minio
  ```
  docker-compose up -d minio
  ```




如何备份es数据?
```bash
elasticdump  --input http://localhost:9200/professor_info --output=./container-data/es/es_backup/professor_info_analyzer.json type=analyzer
elasticdump  --input http://localhost:9200/qa --output=./container-data/es/es_backup/qa_analyzer.json type=analyzer
elasticdump  --input http://localhost:9200/qa --output=./container-data/es/es_backup/qa_mapping.json --type=mapping
elasticdump  --input http://localhost:9200/professor_info --output=./container-data/es/es_backup/professor_info_mapping.json --type=mapping
elasticdump  --input http://localhost:9200/qa --output=./container-data/es/es_backup/qa_data.json type=data
elasticdump  --input http://localhost:9200/professor_info --output=./container-data/es/es_backup/professor_info_data.json type=data
```