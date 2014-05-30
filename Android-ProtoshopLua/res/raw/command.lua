Command=luajava.bindClass("com.ctrip.demo.custom.Command")
function onCreate( context )
	button=Command:createView(context,"android.widget.Button");
	button:setText("Test Lua");
	context:setContentView(button);
end

function onResume( context )
	Command:setText(button);
end