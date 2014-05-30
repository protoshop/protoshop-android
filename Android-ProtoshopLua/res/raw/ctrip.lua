
function getIdentifier(activity,name,type)
	return activity:getResources():getIdentifier(name,type,"com.ctrip.demo")
end

function getImage(activity,name)
	return getIdentifier(activity,name,"drawable")
end

function newImageView(activity)
	return luajava.newInstance("android.widget.ImageView",activity)
end
function newLinearLayout(activity)
	return luajava.newInstance("android.widget.LinearLayout",activity)
end

function newRelativeLayout(activity)
	return luajava.newInstance("android.widget.RelativeLayout",activity)
end



function ctreateCtrip(activity)
	linearLayout=newLinearLayout(activity)
	Color=luajava.bindClass("android.graphics.Color")
	linearLayout:setBackgroundColor(Color:parseColor("#e7f8ff"));
	linearLayout:setOrientation(linearLayout.VERTICAL);

	viewFlow=luajava.newInstance("org.taptwo.android.widget.ViewFlow",activity)
	viewFlow:setId(getIdentifier(activity,"ctrip_viewpage","id"))
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
	viewFlow:setAdapter(ctripAdapter)
	linearLayout:addView(viewFlow)

	Gravity=luajava.bindClass("android.view.Gravity")

	linearLayout2 = newLinearLayout(activity)
	linearLayout2:setOrientation(linearLayout2.HORIZONTAL)
	linearLayout2:setGravity(Gravity.CENTER_VERTICAL)

	LayoutParams=luajava.newInstance("android.view.ViewGroup$LayoutParams",1,1)
	ScaleType=luajava.bindClass("android.widget.ImageView$ScaleType")

	imageView = newImageView(activity)
	planeParams = luajava.newInstance("android.widget.LinearLayout$LayoutParams",LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,1.0)
	imageView:setImageResource(getImage(activity,"plane"));
	imageView:setAdjustViewBounds(true);
	imageView:setScaleType(ScaleType.FIT_XY);
	linearLayout2:addView(imageView,planeParams);

	imageView2 = newImageView(activity)
	trainParams = luajava.newInstance("android.widget.LinearLayout$LayoutParams",LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,0.4)
	imageView2:setImageResource(getImage(activity,"train"));
	imageView2:setAdjustViewBounds(true);
	imageView2:setScaleType(ScaleType.FIT_XY);
	linearLayout2:addView(imageView2, trainParams);

	imageView3 = newImageView(activity)
	carLayoutParams = luajava.newInstance("android.widget.LinearLayout$LayoutParams",LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,0.4)
	imageView3:setImageResource(getImage(activity,"car"));
	imageView3:setAdjustViewBounds(true);
	imageView2:setScaleType(ScaleType.FIT_XY);
	linearLayout2:addView(imageView3, carLayoutParams);

	linearLayout:addView(linearLayout2);

	linearLayout3 = newLinearLayout(activity)
	linearLayout3:setOrientation(linearLayout3.HORIZONTAL);
	linearLayout3:setGravity(Gravity.CENTER_VERTICAL);

	imageViewHotel = newImageView(activity)
	hotelParams = luajava.newInstance("android.widget.LinearLayout$LayoutParams",LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,1.0)
	imageViewHotel:setImageResource(getImage(activity,"hotel"))
	imageViewHotel:setAdjustViewBounds(true);
	imageViewHotel:setScaleType(ScaleType.FIT_XY);
	linearLayout3:addView(imageViewHotel, hotelParams);

	ctripImageView = newImageView(activity)
	ctripParams = luajava.newInstance("android.widget.LinearLayout$LayoutParams",LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,1.0)
	ctripImageView:setImageResource(getImage(activity,"my_ctrip"));
	ctripImageView:setAdjustViewBounds(true);
	imageViewHotel:setScaleType(ScaleType.FIT_XY);
	linearLayout3:addView(ctripImageView, ctripParams);

	linearLayout:addView(linearLayout3);

	activity:setContentView(linearLayout)
end





