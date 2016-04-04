package com.italkyou.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDAO {

	private static final String TAG = BaseDAO.class.getSimpleName();
	private Context context;
	protected SQLiteDatabase db;

	public BaseDAO(Context contexto){

		try {
			context = contexto;
			BaseDatosSQLiteHelper bdslqhelper = new BaseDatosSQLiteHelper(context, TablasBD.BD_NOMBRE_ITY, null, TablasBD.BD_VERSION_ITY);
			db = bdslqhelper.getWritableDatabase();
		}catch (Exception ex){
			Log.e(TAG,"Error en database ", ex);
		}

	}

//
//	protected void openDatabase(Context context){
//		mDb = new BaseDatosSQLiteHelper(context,TablasBD.BD_NOMBRE_ITY,null,TablasBD.BD_VERSION_ITY);
//		this.db = mDb.getReadableDatabase();
//	}
//
//	protected void initDatabase(Context context){
//		try {
//
//			if (db != null) {
//				if (!db.isOpen())
//					openDatabase(context);
//			}
//			else
//				openDatabase(context);
//
//		}catch (Exception ex){
//			Log.e(Const.DEBUG_DATABASE,"Error en initDatabase",ex);
//		};


//	}
	
//	public void cerrar() {
//
//		try {
//			this.db.close();
//		}catch (Exception ex){
//			Log.e(TAG,"Error al cerrar DB " + ex);
//		}
//
//	}
	
	public void borrarTodo(String nombTabla) {

		try {
			this.db.delete(nombTabla, null, null);
			db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+nombTabla+"'");
		}catch (Exception ex){
			Log.e(TAG,"No se puedo Eliminar " + ex);
		}

	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
