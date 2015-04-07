# treeview_android
## Введение
Элемент управления TreeView предоставляет способ отображения данных в виде иерархической структуры с помощью сворачиваемых узлов. Компонент унаследован от стандартного ListView, но добавлена возможность каждому элементу быть группирующим и иметь свои дочерние элементы без ограничения уровня вложенности. Каждый групповой элемент (узел) независимо от других может быть развернут. В этом случае отобразятся все вложенные в него дочерние элементы, которые в свою очередь также могут быть узлами. Элементы могут быть созданы или добавлены в TreeView через адаптер SimpleJsonTreeViewAdapter, связанный с этим TreeView. Адаптер SimpleJsonTreeViewAdapter ориентирован на входные данные в формате Json, наиболее популярный формат получения данных через интернет с WEB-сервера для мобильных устройств.

## Использование
Этот проект может быть включен в пользовательский как внешняя android-библиотека. Также проект может быть использован как часть пользовательского проекта.
Использовать TreeView также просто как и стандартный ListView. Компонент TreeView помещается в layout-ресурс, а в коде только создается адаптер и назначается этому TreeView. Адаптер SimpleJsonTreeViewAdapter имеет конструкторы, очень похожие на стандартные конструкторы ListView и ExpandableListView, поэтому каждому, кто разобрался с этими стандартными компонентами не составит никакого труда начать использовать и TreeView. Демонстрационный проект TreeViewDemo показывает как просто можно это сделать.