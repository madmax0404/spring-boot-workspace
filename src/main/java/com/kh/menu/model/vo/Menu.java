package com.kh.menu.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Menu {
	private int id;
	private String restaurant;
	private String name;
	private int price;
	private String type;
	private String taste;
}
