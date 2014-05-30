function callback(context)
	luajava.loadLib("com.ctrip.demo.MainActivity", "open")-- 调用cn.hpc.common.lua包中的LoadLibExample类的 open方法。
	textView=luajava.newInstance("android.widget.TextView",context)
	
	javaObjcet.methodOpen(textView) -- 返回 两个参数
--	javaObjcet.methodOpen("2", "blog", "http://blog.csdn.net/hpccn") --返回 3 个参数
--	javaObjcet.methodOpen("3", "不同的参数", "http://blog.csdn.net/hpccn")
end