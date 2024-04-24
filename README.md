Проект реализующий файловый менеджер сервер. 
Доступны следующие возможности:
1)Загрузка файлов/каталогов на сервер (файлы из вложенных архивов распаковываются в текущей иерархии) 
2)При загрзке одноименных файлов осуществляется сравнение хэшей и при их расхождении сохраняется файл с указанием версии (при совпадении файл не сохраняется) 
3)Выгрузка запрашиваемых файлов/каталогов

Проект для клиентской части: https://github.com/Dmiit3iy/FileUploaderSpringClient

Используемые технологии:Spring Boot, Hibernate, Lombok, Lingala Zip4j, Jackson, MySql
