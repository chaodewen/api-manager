package com.apilite.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.apilite.Utils;
import com.apilite.db.MongoUtils;
import com.mongodb.client.model.Updates;

public class User {
	@FormParam("id")
	public ObjectId _id;
	
	@FormParam("account")
	public String account;
	
	@FormParam("password")
	public String password;
	
	@FormParam("email")
	public String email;
	
	@FormParam("avatar")
	public String avatar;
	
	@FormParam("phone")
	public String phone;
	
	@FormParam("name")
	public String name;
	
	@FormParam("idno")
	public String idno;
	
	public Date created_time;
	
	public Date updated_time;
	
	/**
	 * FastJson需要默认构造函数帮助解析
	 */
	public User() {}
	
	/**
	 * 以Document类型获取User，密码若存在将进行哈希处理
	 */
	public Document getDocument() {
		Document ret = new Document();
		try {
			Field[] fields = User.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				// 逐个处理，password进行加密
				if(field.get(this) != null && fieldName != "services" ) {
					if(fieldName == "password") {
						ret.put(field.getName()
								, MongoUtils.genHashPassword(this.password));
					}
					else {
						ret.put(field.getName()
								, field.getType().cast(field.get(this)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Document();
		}
		return ret;
	}
	
	/**
	 * 得到更新操作需要的Bson值，出错或无更新时返回null
	 */
	public Bson genUserDocumentUpdates() {
		try {
			this.updated_time = new Date();
			// 利用反射得到所有域
			List<Bson> updates = new ArrayList<Bson>();
			Field[] fields = User.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				// 内容不空且不是无需更新的域
				if(field.get(this) != null && fieldName != "name") {
					if(fieldName == "password") {
						// password要做哈希处理
						updates.add(Updates.setOnInsert(field.getName()
								, MongoUtils.genHashPassword(this.password)));
					}
					else {
						updates.add(Updates.setOnInsert(field.getName()
								, field.getType().cast(field.get(this))));
					}
				}
			}
			return Updates.combine(updates);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * _id不存在时生成一个_id并检查其它必填项是否正常
	 */
	public boolean canPost() {
		if(_id == null) {
			_id = new ObjectId();
		}
		if(created_time == null) {
			created_time = new Date();
			updated_time = created_time;
		}
		else if(updated_time == null){
			updated_time = new Date();
		}
		return Utils.isValid(account, password, email);
	}
	
	/**
	 * 确保_id有非空值
	 * 更新updated_time
	 */
	public boolean canPut() {
		updated_time = new Date();
		return _id != null;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}
}