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


LiveData要实现数据共享，需要设置lifecycleOwner为同一个，不然接受不到，比如设置父Fragment
中的使用this，子Fragment中如果使用子LifecycleOwner，父Fragment中则不会接受到消息

Activity --->Dialog --- >CaputerActiivty---->PreviewActivity 之间数据传递及返回，会通过回调到Activity再手动回调到Dialog中



 @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(new BaseAdapter.AdapterDataObserverProxy(observer));
    }

    //如果我们先添加了headerView,而后网络数据回来了再更新到列表上
    //由于Paging在计算列表上item的位置时 并不会顾及我们有没有添加headerView，就会出现列表定位的问题
    //实际上 RecyclerView#setAdapter方法，它会给Adapter注册了一个AdapterDataObserver
    //咱么可以代理registerAdapterDataObserver()传递进来的observer。在各个方法的实现中，把headerView的个数算上，再中转出去即可
    private class AdapterDataObserverProxy extends RecyclerView.AdapterDataObserver {
        private RecyclerView.AdapterDataObserver mObserver;

        public AdapterDataObserverProxy(RecyclerView.AdapterDataObserver observer) {
            mObserver = observer;
        }

        public void onChanged() {
            mObserver.onChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            mObserver.onItemRangeChanged(positionStart + mHeaders.size(), itemCount);
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mObserver.onItemRangeChanged(positionStart + mHeaders.size(), itemCount, payload);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            mObserver.onItemRangeInserted(positionStart + mHeaders.size(), itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mObserver.onItemRangeRemoved(positionStart + mHeaders.size(), itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mObserver.onItemRangeMoved(fromPosition + mHeaders.size(), toPosition + mHeaders.size(), itemCount);
        }

    }

