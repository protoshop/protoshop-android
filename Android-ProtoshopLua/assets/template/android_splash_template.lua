ScaleType=luajava.bindClass("android.widget.ImageView$ScaleType")
FileUtil = luajava.bindClass("com.demo.tohell.util.FileUtil")
BitmapFactory = luajava.bindClass("android.graphics.BitmapFactory")

function onCreate(activity)
	initSplash(activity,"$appName","$image",$delay,$duration,"scenes")
end

function initSplash(activity,appName,image,delay,duration,target)
	local imageView = luajava.newInstance("android.widget.ImageView",activity)
	local imageFile = FileUtil:getResources(appName,image)
	local bitmap = BitmapFactory:decodeFile(imageFile:getAbsolutePath())
	imageView:setImageBitmap(bitmap)
	imageView:setAdjustViewBounds(true)
	imageView:setScaleType(ScaleType.FIT_XY)
	activity:setContentView(imageView)

	setAnimation(activity,duration,target)
end



function setAnimation( context,duration,target)
	callback={
		handleMessage=function ( msg )
			if msg.what == 1 then
				local util=luajava.bindClass("com.demo.tohell.util.Util")
				local intent = util:createIntent(context,"com.demo.tohell.LuaActivity")
				intent:putExtra("scene", target);
				context:startActivity(intent)
				return true
			end
		end
	}
	local animation = luajava.createProxy("android.os.Handler$Callback",callback)
	local mHandler = luajava.newInstance("android.os.Handler",animation)
	mHandler:sendEmptyMessageDelayed(1, duration);
end