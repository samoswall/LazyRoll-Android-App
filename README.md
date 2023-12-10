# LazyRolls приложение для Android
[![Donate](https://img.shields.io/badge/donate-Yandex-red.svg)](https://yoomoney.ru/fundraise/b8GYBARCVRE.230309)

![all](https://github.com/samoswall/LazyRoll-Android-App/blob/master/LazyRollsApk.png)
## Приложение отображает текущее состояние моторизированных приводов для рулонных штор в которых используется прошивка LazyRolls и позволяет ими управлять

> [!NOTE]
> Важно! **Пока** управление производится по IP адресам, поэтому если смениться IP, то устройство станет недоступно.
> Требуется его удалить и добавить заново. Или использовать статические адреса. (См. список доработок)

# Как пользоваться
На главном экране через меню добавляем комнату. У комнаты есть настройки (3 точки).
При смене картинки на фото из галереи телефона, спросит разрешение на доступ к файловой системе (естественно разрешить).
Кнопки на комнате управляют всеми устройствами в комнате.

Кликом по комнате (не по ее кнопкам), попадаем на экран управления приводами.
Плюсом добавляем устройства в комнату (если у устройства нет имени, оно будет заменено на имя в сети).
У каждого устройства есть настройки (3 точки) настраиваем как надо.

Обновление информации от приводов осуществляется раз в 2 секунды, только тех приводов, которые добавлены в комнату. 
На главном экране информация не обновляется, берется из конфига. Конфиг сохраняется при выходе из приложения по двойному клику.

Управление ведомыми будет добавлено после выхода прошивки v 0.15 (может и раньше).

В приложении реализована проверка новых версий! В случае доступности новой версии, приложение об этом сообщит красным значком на главном экране. 

# Список для доработок

- [X] :heavy_check_mark: Поддержка темной темы
- [X] :heavy_check_mark: Отображение при выходе шторы в минус
- [X] :heavy_check_mark: Проверка новых версий приложения
- [X] :heavy_check_mark: Адаптация под разные разрешения и плотности пикселей экранов
- [ ] Обрезать длинные имена (потому что не умещаются)
- [ ] Экспорт настроек
- [ ] Добавить ручной ввод кода цвета шторы
- [ ] Поддержка альбомной ориентации и/или планшетов
- [ ] Поддержка MQTT
- [ ] Управление WiFi телефона (включить если выключен)
- [ ] Добавить слайдер положения шторы

> [!NOTE]
> Иногда на приводе может появиться статус Устройство не доступно.
> Это говорит о том, что устройство 3 раза не ответило на запрос (опрос каждые 2 секунды). Если устройство ответило, счетчик отсутствующих ответов снова становится равным 3.