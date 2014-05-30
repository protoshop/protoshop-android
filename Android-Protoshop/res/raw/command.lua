Command = luajava.bindClass("com.protoshop.lua.Command")
function onCreate( activity )
	id1386064627840=Command:createScence(activity,"dbf49c1cd747ff88a317b82dd1ecf476", "1397717678350.png")
	idImageView5346cd0dd06f46a4a7bec21f3b2d5eff0032060=Command:createView(activity,'ImageView')

Command:setAttr(id1386064627840,idImageView5346cd0dd06f46a4a7bec21f3b2d5eff0032060,'{"elements": [{"textFont": "", "elements": {}, "bgColor": {"bkColorBlue": 1.0, "bkAlpha": 1.0, "bkColorRed": 1.0, "bkColorGreen": 1.0}, "text": "\u4e0a\u6d77-\u6606\u660e", "height": 20, "width": 250, "textFontSize": 16, "y": 5, "x": 70, "textColor": {"bkColorBlue": 0.0, "bkAlpha": 1.0, "bkColorRed": 0.0, "bkColorGreen": 0.0}, "type": "Label", "alignment": 1}, {"textFont": "", "elements": {}, "bgColor": {"bkColorBlue": 1.0, "bkAlpha": 1.0, "bkColorRed": 1.0, "bkColorGreen": 1.0}, "text": "2014-04-30", "height": 20, "width": 250, "textFontSize": 13, "y": 25, "x": 70, "textColor": {"bkColorBlue": 0.0, "bkAlpha": 1.0, "bkColorRed": 0.0, "bkColorGreen": 0.0}, "type": "Label", "alignment": 1}, {"elements": {}, "actions": [{"target": 1386064627839, "transitionDirection": "right", "transitionDelay": 0, "transitionType": "push", "type": "jumpto", "transitionDuration": 0.75}], "height": 60, "width": 60, "y": 0, "x": 10, "type": "Button"}], "height": 60, "width": 320, "imageName": "image.png", "y": 0, "x": 0, "type": "ImageView"}','')
 
idLabel0322ecb0a9de42b2a907166d9c55d01170525020=Command:createView(activity,'Label')

Command:setAttr(idImageView5346cd0dd06f46a4a7bec21f3b2d5eff0032060,idLabel0322ecb0a9de42b2a907166d9c55d01170525020,'{"textFont": "", "elements": {}, "bgColor": {"bkColorBlue": 1.0, "bkAlpha": 1.0, "bkColorRed": 1.0, "bkColorGreen": 1.0}, "text": "\u4e0a\u6d77-\u6606\u660e", "height": 20, "width": 250, "textFontSize": 16, "y": 5, "x": 70, "textColor": {"bkColorBlue": 0.0, "bkAlpha": 1.0, "bkColorRed": 0.0, "bkColorGreen": 0.0}, "type": "Label", "alignment": 1}','上海-昆明')
 
idLabeldc6a26f7eaab4c538b906e3c5cc68cce702525020=Command:createView(activity,'Label')

Command:setAttr(idImageView5346cd0dd06f46a4a7bec21f3b2d5eff0032060,idLabeldc6a26f7eaab4c538b906e3c5cc68cce702525020,'{"textFont": "", "elements": {}, "bgColor": {"bkColorBlue": 1.0, "bkAlpha": 1.0, "bkColorRed": 1.0, "bkColorGreen": 1.0}, "text": "2014-04-30", "height": 20, "width": 250, "textFontSize": 13, "y": 25, "x": 70, "textColor": {"bkColorBlue": 0.0, "bkAlpha": 1.0, "bkColorRed": 0.0, "bkColorGreen": 0.0}, "type": "Label", "alignment": 1}','2014-04-30')
 
idButton81547156696c4ea4a9461be6f66e6f861006060=Command:createView(activity,'Button')

Command:setAttr(idImageView5346cd0dd06f46a4a7bec21f3b2d5eff0032060,idButton81547156696c4ea4a9461be6f66e6f861006060,'{"elements": {}, "actions": [{"target": 1386064627839, "transitionDirection": "right", "transitionDelay": 0, "transitionType": "push", "type": "jumpto", "transitionDuration": 0.75}], "height": 60, "width": 60, "y": 0, "x": 10, "type": "Button"}','')
 
Command:setAction(idButton81547156696c4ea4a9461be6f66e6f861006060,'{"target": 1386064627839, "transitionDirection": "right", "transitionDelay": 0, "transitionType": "push", "type": "jumpto", "transitionDuration": 0.75}')


idScrollView1a50a40748bd47b594fa020678cdffa8060320508=Command:createView(activity,'ScrollView')

Command:setAttr(id1386064627840,idScrollView1a50a40748bd47b594fa020678cdffa8060320508,'{"elements": [{"elements": {}, "height": 200, "width": 320, "imageName": "11.png", "y": 0, "x": 0, "type": "ImageView"}, {"elements": {}, "height": 200, "width": 320, "imageName": "12.png", "y": 200, "x": 0, "type": "ImageView"}, {"elements": {}, "height": 200, "width": 320, "imageName": "13.png", "y": 400, "x": 0, "type": "ImageView"}, {"elements": {}, "height": 200, "width": 320, "imageName": "14.png", "y": 600, "x": 0, "type": "ImageView"}, {"elements": {}, "height": 200, "width": 320, "imageName": "15.png", "y": 800, "x": 0, "type": "ImageView"}], "orientation": "vertical", "bgColor": {"bkColorBlue": 1.0, "bkAlpha": 1.0, "bkColorRed": 1.0, "bkColorGreen": 1.0}, "height": 508, "width": 320, "contentOffset": {"anchorOriginX": 0, "anchorOriginY": 0}, "y": 60, "x": 0, "type": "ScrollView", "contentSize": {"contentSizeWidth": 320, "contentSizeHeight": 1016}}','')
 
idImageView510f2ea80ef54197aae126f14d1280e000320200=Command:createView(activity,'ImageView')

Command:setAttr(idScrollView1a50a40748bd47b594fa020678cdffa8060320508,idImageView510f2ea80ef54197aae126f14d1280e000320200,'{"elements": {}, "height": 200, "width": 320, "imageName": "11.png", "y": 0, "x": 0, "type": "ImageView"}','')
 
idImageView43640d3a70b14e70bcc215e14839347b0200320200=Command:createView(activity,'ImageView')

Command:setAttr(idScrollView1a50a40748bd47b594fa020678cdffa8060320508,idImageView43640d3a70b14e70bcc215e14839347b0200320200,'{"elements": {}, "height": 200, "width": 320, "imageName": "12.png", "y": 200, "x": 0, "type": "ImageView"}','')
 
idImageViewf19cb3eee0fe4093a8185d6b77d0d11a0400320200=Command:createView(activity,'ImageView')

Command:setAttr(idScrollView1a50a40748bd47b594fa020678cdffa8060320508,idImageViewf19cb3eee0fe4093a8185d6b77d0d11a0400320200,'{"elements": {}, "height": 200, "width": 320, "imageName": "13.png", "y": 400, "x": 0, "type": "ImageView"}','')
 
idImageViewaada9b8d5d424391999172597d9cf36a0600320200=Command:createView(activity,'ImageView')

Command:setAttr(idScrollView1a50a40748bd47b594fa020678cdffa8060320508,idImageViewaada9b8d5d424391999172597d9cf36a0600320200,'{"elements": {}, "height": 200, "width": 320, "imageName": "14.png", "y": 600, "x": 0, "type": "ImageView"}','')
 
idImageViewb2a7df991ee3453a879ad5ebb6edebd40800320200=Command:createView(activity,'ImageView')

Command:setAttr(idScrollView1a50a40748bd47b594fa020678cdffa8060320508,idImageViewb2a7df991ee3453a879ad5ebb6edebd40800320200,'{"elements": {}, "height": 200, "width": 320, "imageName": "15.png", "y": 800, "x": 0, "type": "ImageView"}','')
 



end
