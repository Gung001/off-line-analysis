package com.lxgy.analysis.track.enums;

import com.lxgy.analysis.core.utils.EnumUtil;

/**
 * 订单类型
 * @author Gryant
 */

public enum OrderChargeTypeEnum {

	SUCCESS(0, "成功"),
	REFUND(1, "退款"),
	;

	private Integer code;
	private String desc;

	private OrderChargeTypeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
		EnumUtil.put(this.getClass().getName() + code, this);
	}

	/**
	 * <pre>
	 * 一个便利的方法，方便使用者通过code获得枚举对象，
	 * 对于非法状态，以个人处理&lt;/b&gt;
	 * </pre>
	 * 
	 * @param code
	 * @return
	 */
	public static OrderChargeTypeEnum valueOf(Integer code) {
		Object obj = EnumUtil.get(OrderChargeTypeEnum.class.getName() + code);
		if (null != obj) {
			return (OrderChargeTypeEnum) obj;
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
