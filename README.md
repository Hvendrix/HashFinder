# HashFinder
App for checking hash

   Здравствуйте! Приложение состоит из двух активити.
Одно для проверки наличия хеша в списке хешей
(для хранения и поиска используется HashSet)
и для добавления в случае отсутствия.
Второе активити для экспорта/импорта в файл.

  Проверка присутствия хеша в HashSet ведется с помощью
службы в отдельном процессе. Также для удобство демонстрации
работы приложения добавлено menu с кнопками для генерации одного значения
в поле для ввода и для добавления сразу многих случайных значений.
Во втором активити кнопки экспорта/импорта ведут в соответствующий диалог. 

   Расширение файла ".txt" вводить не нужно,
оно добавляется автоматически. Задача обеспечения
сохранения данных при смене состояния устройства решена
с помощью паттерна MVVM. Локализация приложения
обеспечивается разными ресурсами для разных
настроек языка в самом телефоне.
Спасибо, что дочитали до конца!