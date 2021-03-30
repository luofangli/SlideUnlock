package com.example.my_2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    //在这儿将视图存在数组中需要使用懒加载，
    //对象的创建：-》(构造方法——》init方法，属性的创建——》oncreate{setContentView})
    //只有在create()方法结束之后，该类的对象及其所有的属性才会被初始化完毕
    //懒加载是只用在用的时候才会用 ,懒加载必须要用val
    //val->不能再被另外赋值，不可变，var——》还可变的,能够被赋予相同类型的其他值

    //跳转到相机的请求码
    private val REQUEST_IMGE_CODE = 1

    //保存所有被点亮的点
    private val allselectedView = arrayListOf<ImageView>()
    //保存密码
    private val password = StringBuilder()
    //保存线的tag值
    private val allslineTag = arrayOf(12,23,45,56,78,89,
    14,25,36,47,58,69,15,26,48,59,24,35,57,68)
    //用于记录上一个的圆点
    private var lastDot:ImageView? = null

    private val dots:Array<ImageView>by lazy {
        arrayOf(sdot1,sdot2,sdot3,sdot4,sdot5,sdot6,sdot7,sdot8,sdot9)
    }
    //算出导航栏的高度，因为导航栏的高度是固定的，所以可以只算一次就好了，就可以用懒加载了
    //懒加载只加载一次
    private val barHeight:Int by lazy {
        //获取屏幕的高度度
        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)
        //获取绘制区域的高度
        val drawingRect = Rect()
        window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT).getDrawingRect(drawingRect)
        return@lazy display.heightPixels-drawingRect.height()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //保存图片
        File(filesDir,"header.jpg").also {
            if (it.exists()){
                BitmapFactory.decodeFile("${filesDir.path}/header.jpg").also {img->
                    headerImage.setImageBitmap(img)
                }
            }
        }
        SharePrefrenceUtil.getInstance(this).getPassword().apply {
            if (this == null){
                pwdText.text = "请绘制密码"
            }else{
                pwdText.text = "请输入密码"
            }
        }

        headerImage.setOnClickListener {
            Intent().apply{
                action = Intent.ACTION_PICK
                setType("image/*")
            }.also {
                startActivityForResult(it,REQUEST_IMGE_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMGE_CODE){
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let {
                    //将图片写入本地
                    contentResolver.openInputStream(it).use {
                        //bitmap
                        BitmapFactory.decodeStream(it).also {image->
                            //显示图片
                            headerImage.setImageBitmap(image)
                            //将图片缓存起来
                            val file = File(filesDir,"header.jpg")
                            FileOutputStream(file).also {fos->
                                //将图片缓存到fos对应的路径中
                                image.compress(Bitmap.CompressFormat.JPEG,50,fos)
                            }
                        }
                    }
                }
            }
            }
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val point = event?.let { toMContainer(it) }
        //限制点击事件接收的区域
        if ((point?.x!! >=0&& point.x <mcontainer.width)&&
            (point.y >=0&& point.y <=mcontainer.height)){
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    //改变视图状态
                    findPointContainsView(point)?.apply {
                        higheLigheView(this)
                    }
//                pointInview(point).apply {
//                   if (this != null){
//                       visibility = View.VISIBLE
//                   }
//                }
                }
                MotionEvent.ACTION_MOVE->{
                    //改变视图状态
                    findPointContainsView(point)?.apply {
                        higheLigheView(this)
                    }
//                pointInview(point).apply {
//                    if (this != null){
//                        Log.v("lfl","进来了")
//                        setVisible(true)
//                    }else{
//                        Log.v("lfl","视图为空")
//                    }
//                }
                }
                MotionEvent.ACTION_UP->reset()
        }

        }
        return true
    }


    //获取视图对应的rect
    private fun getRectForView(v:ImageView) = Rect(v.left,v.top,v.right,v.bottom)
   //查询当前的触摸点所在的圆点
    private fun findPointContainsView(point: Point):ImageView?{
       for (img in dots){
           if (getRectForView(img).contains(point.x,point.y)){
               return img
           }
       }
       return null
   }

//    //将判断被点的位置是否在圆点视图内
//    private fun pointInview(point: Point):ImageView?{
//       for (imag in dots){
//           orPoint(Rect(imag.left,imag.top,imag.right,imag.bottom),point).apply {
//               if (true){
//                  return imag
//               }
//           }
//       }
//        return null
//    }

    //判断点是否在某一个点内
//    private fun orPoint(rect: Rect,point: Point):Boolean{
//        if (rect.contains(point.x,point.y)){
//             return true
//        }
//        return false
//    }
    //将event点的坐标转换为相对于圆点父容器的坐标
    private fun toMContainer(event: MotionEvent):Point{
        return Point().apply {
            x = (event.x - mcontainer.x).toInt()
            y = (event.y-barHeight-mcontainer.y).toInt()
        }
    }
    //点亮视图
    private fun higheLigheView(v:ImageView){
        if (lastDot == null){
        if (v.visibility != View.VISIBLE){
            v.visibility = View.VISIBLE
            //lastDot指在该视图上
            lastDot = v
            //将点亮的视图加入到被点亮的视图中
            allselectedView.add(v)
            //拼接密码
            password.append(v.tag)
        }
        }else{
            val lastTag = lastDot?.tag.toString().toInt()
            val newTag = v.tag.toString().toInt()
          if (lastTag>newTag){
              val tempTag = newTag*10+lastTag
              //遍列tag值，
              for (tag in allslineTag){
                  if (tag == tempTag){
                   val view=   mcontainer.findViewWithTag<ImageView>(tag.toString())
                      if (view != null){
                          view.visibility = View.VISIBLE
                          v.visibility = View.VISIBLE
                          allselectedView.add(view)
                          lastDot = v
                          allselectedView.add(v)
                          password.append(v.tag)    }

                  }
              }
          }else{
              val tempTag = lastTag*10 + newTag
              //遍列tag值，
              for (tag in allslineTag){
                  if (tag == tempTag){
                    val view =  mcontainer.findViewWithTag<ImageView>(tag.toString())
                      if (view != null){
                         view.visibility = View.VISIBLE
                          v.visibility = View.VISIBLE
                          allselectedView.add(view)
                          lastDot = v
                          allselectedView.add(v)
                          password.append(v.tag)
                      }

                  }
              }
          }
        }
    }
    //还原操作
    private fun reset(){
        SharePrefrenceUtil.getInstance(this).apply {
           getPassword().also {
               if (it == null){
                   savePassword(password.toString())
                   pwdText.text = "请确认密码"
               }else{
                   if (it == password.toString()){
                       pwdText.text = "输入密码成功"
                   }else{
                       clearpassword()
                       pwdText.text = "请重新绘制密码"
                   }
               }
           }
        }
        for (iteam in allselectedView){
            iteam.visibility = View.INVISIBLE
        }
        //清空 这两个并不是用来保存密码的，是记录密码的，记录密码看是否与保存的密码相同
        allselectedView.clear()
        password.clear()
        lastDot = null
    }
}