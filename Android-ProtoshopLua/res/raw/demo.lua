function onCreate(activity)
	textView=luajava.newInstance("android.widget.TextView",activity);
	textView:setText("Hello,Lua!");
	activity:setContentView(textView);
end

function onResume( activity )
	textView:setText("Hello,Resume Lua!");
end

