package com.vvu981.chronoslink.dto;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.CapsuleStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class CapsuleDTO {

            String title;
            String content; // Este solo tendrá valor si la cápsula está OPENED
            CapsuleStatus status;
            LocalDateTime openAt;
            LocalDateTime createdAt;

            public CapsuleDTO(Capsule capsule) {
                this.title = capsule.getTitle();
                this.content = capsule.getContent();
                this.createdAt = capsule.getCreatedAt();
                this.openAt = capsule.getOpenAt();
                this.createdAt = capsule.getCreatedAt();
            }

}
