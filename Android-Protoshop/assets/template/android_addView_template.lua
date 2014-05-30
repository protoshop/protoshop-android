params=luajava.newInstance("android.widget.AbsoluteLayout#LayoutParams",#width*WHPer.wPer,#height*WHPer.hPer,#x*WHPer.wPer,#y*WHPer.hPer)
button=luajava.newInstance("android.widget.Button",activity)
button:setBackgroundDrawable(null)
--设置action事件
	#setAction
viewContainer:addView(button,params)