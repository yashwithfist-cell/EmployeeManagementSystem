package com.pmtool.backend.DTO.request;

import com.pmtool.backend.enums.TaskStatus;

import lombok.Data;

@Data
public class HeadActionRequest {
	private TaskStatus taskStatus;
    private String headComment;
}
