package com.example.m450_lb1.data.model.dto;

import com.example.m450_lb1.data.model.dao.User;
import lombok.Data;

@Data
public class ReturnRequest {
	String bookName;
	User user;
}
