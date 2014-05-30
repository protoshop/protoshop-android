BitmapUtil=luajava.bindClass("com.demo.tohell.util.BitmapUtil")
require("button")
function onCreate( activity )
	init(activity,"test1","bacgroud1.jpg")
	initButton(activity,"按钮",200,100,140,350)
	addView(button,params)
end

function init( activity,appName,backgroud )
	viewContainer  = luajava.newInstance("android.widget.AbsoluteLayout",activity)
	viewContainer:setBackgroundDrawable(BitmapUtil:getBitmapDrawable(appName,backgroud))
	activity:setContentView(viewContainer)
end

function addView( view ,params)
	viewContainer:addView(view,params)
end
