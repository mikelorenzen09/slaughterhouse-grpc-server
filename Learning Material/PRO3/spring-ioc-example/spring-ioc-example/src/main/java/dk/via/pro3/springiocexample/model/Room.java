package dk.via.pro3.springiocexample.model;

public class Room
{
  private int id;
  private String name;

  public Room(int id, String name)
  {
    this.id = id;
    this.name = name;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getId()
  {
    return id;
  }

  public String toString()
  {
    return "Room{id=" + id + ", name='" + name + "'}";
  }
  public void roomService()
  {
    System.out.println("\nHere is your drink ");
    System.out.println("Delivered to room " + id + " (" + name + ")");
  }

  public String getName()
  {
    return name;
  }
}
