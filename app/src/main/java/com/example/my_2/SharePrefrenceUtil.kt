package com.example.my_2

import android.content.Context

class SharePrefrenceUtil private constructor(){
    private val FILE_NAME = "password"
    private val KEY = "passwordkey"
    companion object{
        //为了保证整个程序的运行过程中只能SharePerenceUtil,
        /*要调用sharedpreference就必须要用context，而该类中没有继承activity，
        是没有context的,所以要在该类中使用sharedpreference就通过instence传递过来的
        context给mcontext继而能够调用sharedpreference

         */
        private var mcontext:Context? = null
        private var instance:SharePrefrenceUtil ? = null
        fun getInstance(context: Context) :SharePrefrenceUtil {
            mcontext = context
            if (instance == null){
                synchronized(this){
                    instance = SharePrefrenceUtil()
                }
            }
            return instance !!
        }
    }
    fun savePassword(pwd:String){
        /*
        sharedpreference是生成一个xml文件用于管理存进去的数据
       FILE_NAME：xml文件的名字
       Context.MODE_PRIVATE:是管理文件的模式，这里是指这个文件是私有的，只能本应用程序调用，
                              外部程序是无法调用的
         */
        //获取sharedpreference对象
        val sharedPreferences = mcontext?.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        //获取edit-》是用于写入数据
        val sharededit = sharedPreferences?.edit()
        //写入数据
        sharededit?.putString(KEY,pwd)
        sharededit?.apply()
    }
    fun getPassword():String?{
        //获取sharedpreference
        val sharedPreferences = mcontext?.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        //获取数据
        return sharedPreferences?.getString(KEY,null)
    }
    fun clearpassword(){
        //获取sharedpreference
      val sharedPreferences = mcontext?.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
      sharedPreferences?.edit()?.clear()?.apply()
    }
}