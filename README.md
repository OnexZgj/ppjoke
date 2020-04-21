# ppjoke

皮皮虾最佳实践注意事项

Paging框架中刷新逻辑的使用
```
    mViewModel.getDataSource().invalidate();
    mViewModel.getDataSource().invalid();

```


这个是什么鬼
Pair<Integer, Integer>


在代码中修改TextView的DrawableRight图片
setCompoundDrawables(Drawable left,Drawable top,Drawable right,Drawable bottom)

Drawable nav_up=getResources().getDrawable(R.drawable.button_nav_up);
nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
textview1.setCompoundDrawables(null, null, nav_up, null);