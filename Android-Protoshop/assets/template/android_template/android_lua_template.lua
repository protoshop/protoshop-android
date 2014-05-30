Command = luajava.bindClass("com.protoshop.lua.Command")
function onCreate( activity )
	#scence=Command:createScence(activity,"#appID", "#backgroud")
	#addView
end

--程序再次进入
function onResume( activity )
end

--程序进入后台
function onStop( activity )
end