
function getIdentifier(activity,name,type)
	return activity:getResources():getIdentifier(name,type,"com.ctrip.demo")
end

function getImage(activity,name)
	return getIdentifier(activity,name,"drawable")
end

function newImageView(activity)
	return luajava.newInstance("android.widget.ImageView",activity)
end

function parseAndroid(activity)

ScaleType=luajava.bindClass("android.widget.ImageView$ScaleType")

	container=luajava.newInstance("android.widget.RelativeLayout",activity)
	container:setId(activity:getResources():getIdentifier("container","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.FrameLayout$LayoutParams",-2,-1)
	
	Color=luajava.bindClass("android.graphics.Color")
	container:setBackgroundColor(Color:parseColor("#e7f8ff"));

	ScrollView0=luajava.newInstance("android.widget.ScrollView",activity)
	LayoutParams0=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-2,-1)
	LayoutParams0:addRule(2,activity:getResources():getIdentifier("imageView9","id","com.ctrip.demo"))
	ScrollView0:setVerticalScrollBarEnabled(false)
	ScrollView0:setScrollbarFadingEnabled(false)
	ScrollView0:setHorizontalScrollBarEnabled(false)
	ScrollView0:setHorizontalFadingEdgeEnabled(false)
	container:addView(ScrollView0,LayoutParams0)

	relativeLayout1=luajava.newInstance("android.widget.RelativeLayout",activity)
	relativeLayout1:setId(activity:getResources():getIdentifier("relativeLayout1","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.FrameLayout$LayoutParams",-1,-1)
	ScrollView0:addView(relativeLayout1,LayoutParams0)

	viewFlow1=luajava.newInstance("org.taptwo.android.widget.ViewFlow",activity)
	viewFlow1:setId(activity:getResources():getIdentifier("viewFlow1","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-1,-2)
	LayoutParams0:addRule(10);
	relativeLayout1:addView(viewFlow1,LayoutParams0)

	baseAdapterInterface={
		getView=function ( position,convertView,parent)
			imageView = newImageView(activity)
			imageView:setAdjustViewBounds(true)
			imageView:setImageResource(getImage(activity,"adver"))
			return imageView
		end,
		getItemId=function(position)
			return position
		end,
		getItem=function ( position )
			return position
		end,
		getCount=function (  )
			return 3
		end
	}
	local baseAdapter = luajava.createProxy("com.ctrip.demo.bridge.BaseAdapterInterface",baseAdapterInterface)
	ctripAdapter=luajava.newInstance("com.ctrip.demo.custom.CtripAdapter",baseAdapter)
	viewFlow1:setAdapter(ctripAdapter)
	
	local touchListener=luajava.newInstance("com.ctrip.demo.bridge.CtripTouchListener");
	viewFlow1:setOnTouchListener(touchListener)
	

	line1=luajava.newInstance("android.widget.LinearLayout",activity)
	line1:setId(activity:getResources():getIdentifier("line1","id","com.ctrip.demo"))
	LayoutParams1=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-1,-2)
	LayoutParams1:addRule(3,activity:getResources():getIdentifier("viewFlow1","id","com.ctrip.demo"))
	relativeLayout1:addView(line1,LayoutParams1)

	imageView1=luajava.newInstance("android.widget.ImageView",activity)
	imageView1:setId(activity:getResources():getIdentifier("imageView1","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,1.0)
	imageView1:setAdjustViewBounds(true)
	imageView1:setImageResource(getImage(activity,"plane"))
	imageView1:setScaleType(ScaleType.FIT_XY);
	line1:addView(imageView1,LayoutParams0)

	imageView2=luajava.newInstance("android.widget.ImageView",activity)
	imageView2:setId(activity:getResources():getIdentifier("imageView2","id","com.ctrip.demo"))
	LayoutParams1=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,0.4)
	imageView2:setAdjustViewBounds(true)
	imageView2:setImageResource(getImage(activity,"train"))
	imageView2:setScaleType(ScaleType.FIT_XY);
	line1:addView(imageView2,LayoutParams1)

	imageView3=luajava.newInstance("android.widget.ImageView",activity)
	imageView3:setId(activity:getResources():getIdentifier("imageView3","id","com.ctrip.demo"))
	LayoutParams2=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,0.3)
	imageView3:setAdjustViewBounds(true)
	imageView3:setScaleType(ScaleType.FIT_XY);
	imageView3:setImageResource(getImage(activity,"car"))
	line1:addView(imageView3,LayoutParams2)

	line2=luajava.newInstance("android.widget.LinearLayout",activity)
	line2:setId(activity:getResources():getIdentifier("line2","id","com.ctrip.demo"))
	LayoutParams2=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-1,-2)
	LayoutParams2:addRule(3,activity:getResources():getIdentifier("line1","id","com.ctrip.demo"))
	relativeLayout1:addView(line2,LayoutParams2)

	imageView4=luajava.newInstance("android.widget.ImageView",activity)
	imageView4:setId(activity:getResources():getIdentifier("imageView4","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-1,-2,1.0)
	imageView4:setAdjustViewBounds(true)
	imageView4:setImageResource(getImage(activity,"hotel"))
	line2:addView(imageView4,LayoutParams0)

	imageView5=luajava.newInstance("android.widget.ImageView",activity)
	imageView5:setId(activity:getResources():getIdentifier("imageView5","id","com.ctrip.demo"))
	LayoutParams1=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-1,-2,1.0)
	imageView5:setAdjustViewBounds(true)
	imageView5:setScaleType(ScaleType.CENTER);
	imageView5:setImageResource(getImage(activity,"my_ctrip"))
	line2:addView(imageView5,LayoutParams1)

	line3=luajava.newInstance("android.widget.LinearLayout",activity)
	line3:setId(activity:getResources():getIdentifier("line3","id","com.ctrip.demo"))
	LayoutParams3=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-1,-2)
	LayoutParams3:addRule(3,activity:getResources():getIdentifier("line2","id","com.ctrip.demo"))
	relativeLayout1:addView(line3,LayoutParams3)

	imageView8=luajava.newInstance("android.widget.ImageView",activity)
	imageView8:setId(activity:getResources():getIdentifier("imageView8","id","com.ctrip.demo"))
	LayoutParams0=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,1.0)
	imageView8:setAdjustViewBounds(true)
	imageView8:setScaleType(ScaleType.FIT_XY);
	imageView8:setImageResource(getImage(activity,"tourism"))
	line3:addView(imageView8,LayoutParams0)

	imageView6=luajava.newInstance("android.widget.ImageView",activity)
	imageView6:setId(activity:getResources():getIdentifier("imageView6","id","com.ctrip.demo"))
	LayoutParams1=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,1.0)
	imageView6:setAdjustViewBounds(true)
	imageView6:setScaleType(ScaleType.FIT_XY);
	imageView6:setImageResource(getImage(activity,"strategy"))
	line3:addView(imageView6,LayoutParams1)

	imageView7=luajava.newInstance("android.widget.ImageView",activity)
	imageView7:setId(activity:getResources():getIdentifier("imageView7","id","com.ctrip.demo"))
	LayoutParams2=luajava.newInstance("android.widget.LinearLayout$LayoutParams",-2,-1,1.0)
	imageView7:setAdjustViewBounds(true)
	imageView7:setScaleType(ScaleType.FIT_XY);
	imageView7:setImageResource(getImage(activity,"ticket"))
	line3:addView(imageView7,LayoutParams2)

	imageView9=luajava.newInstance("android.widget.ImageView",activity)
	imageView9:setId(activity:getResources():getIdentifier("imageView9","id","com.ctrip.demo"))
	LayoutParams1=luajava.newInstance("android.widget.RelativeLayout$LayoutParams",-2,-2)
	LayoutParams1:addRule(12)
	imageView9:setAdjustViewBounds(true)
	imageView9:setImageResource(getImage(activity,"bottom"))
	container:addView(imageView9,LayoutParams1)


	activity:setContentView(container)


end