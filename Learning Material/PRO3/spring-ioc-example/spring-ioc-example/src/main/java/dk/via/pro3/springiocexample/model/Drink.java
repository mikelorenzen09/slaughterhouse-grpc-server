package dk.via.pro3.springiocexample.model;

import org.springframework.stereotype.Component;

@Component
public class Drink
{
  private int Id;
  private String name;
  private String description;
  private float price;

  private final Room room;


  public Drink(Room room)
  {
    this.room = room;
  }

  public void setId(int id)
  {
    Id = id;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setPrice(float price)
  {
    this.price = price;
  }
  public void confirmObjectCreation()
  {
    System.out.println("Drink Object created and injected successfully");
    room.roomService();
  }
}
