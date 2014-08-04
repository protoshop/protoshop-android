click = {
	onClick = function(view)
		--view:setBackgroundDrawable(BitmapUtil:getBitmapDrawable("#appID","#normal"))
		local context = view:getContext()
		local intent = LuaUtil:createIntent(context,"com.protoshop.lua.LuaActivity")
		intent:putExtra("scene", "#target");
		intent:putExtra("appID","#appID");
		context:startActivity(intent)
	end
}
local listener = luajava.createProxy("android.view.View#OnClickListener", click);
button:setOnClickListener(listener)