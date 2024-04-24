package com.example.quiz.model;

import com.example.quiz.util.Dates;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.util.Date;

@Entity
@SqlResultSetMapping(name = "PlayerOut")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerOut {

    @Id
    private Long id;

    private Date createdat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdat")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdat);
    }

    private String fullname;







    public Date getCreatedat() {
        return createdat;
    }

    public String getFullname() {
        return fullname;
    }



    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }







    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
