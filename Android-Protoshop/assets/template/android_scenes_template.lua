LuaBitmapUtil=luajava.bindClass("com.protoshop.lua.LuaBitmapUtil")
LuaUtil=luajava.bindClass("com.protoshop.lua.LuaUtil")
--程序入口，代码中调用
function onCreate( activity )
	init(activity)
end

function init( activity )
	WHPer = luajava.newInstance("com.protoshop.lua.WHPer",activity)
	viewContainer  = luajava.newInstance("android.widget.AbsoluteLayout",activity)
	activity:setContentView(viewContainer)
	--添加子控件占位符
	#addView
end

--程序再次进入
function onResume( activity )
	--设置背景图片 appName:项目名称 backgroud:背景图片
	BitmapDrawable=LuaBitmapUtil:getBitmapDrawable(activity,"#appID","#backgroud");
	viewContainer:setBackgroundDrawable(BitmapDrawable);
end

--程序进入后台
function onStop( activity )
	viewContainer:setBackgroundDrawable(null);
	LuaBitmapUtil:recycleBitmap(BitmapDrawable);
end

