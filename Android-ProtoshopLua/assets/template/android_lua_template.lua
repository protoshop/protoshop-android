Command = luajava.bindClass("com.protoshop.lua.Command")
function onCreate( activity )
	scence=Command:createScence(activity,"#appID", "#backgroud");
end