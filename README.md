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


backgroundTint 的使用

Dialog填充完以后再设置，否则无法生效
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


文件上传用的到
    //AtomicInteger, CountDownLatch, CyclicBarrier
//        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);


 PagelistAdapter中的数据监测变化改变监听

            HomeFragment中可以查看

           @Override
             public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                 if (previousList != null && currentList != null) {
                     if (!currentList.containsAll(previousList)) {
                         //则表示是刷新逻辑,滑动到第一个item的位置
                         mRecyclerView.scrollToPosition(0);
                     }
                 }
             }


Activity --->Dialog --- >CaputerActiivty---->PreviewActivity 之间数据传递及返回，会通过回调到Activity再手动回调到Dialog中