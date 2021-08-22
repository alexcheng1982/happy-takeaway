package io.vividcode.happytakeaway.restaurant.entity;

import io.vividcode.happytakeaway.common.entity.TimestampedBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntity extends TimestampedBaseEntity {

  @Column(name = "owner_id")
  @Size(min = 1, max = 50)
  private String ownerId;

  @Column(name = "name")
  @Size(min = 1, max = 128)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "phone_number")
  @Size(min = 1, max = 20)
  private String phoneNumber;

  @Column(name = "address_code")
  @Size(min = 1, max = 12)
  private String addressCode;

  @Column(name = "address_line")
  @Size(min = 1, max = 128)
  private String addressLine;

  @Column(name = "address_lng")
  private double addressLng;

  @Column(name = "address_lat")
  private double addressLat;

  @OneToOne
  @JoinColumn(name = "active_menu_id")
  private MenuEntity activeMenu;
}
