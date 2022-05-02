package com.example;

import com.example.query.api.Entity;
import com.example.query.api.Query;
import manifold.ext.props.rt.api.val;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.example.Gender.female;
import static com.example.Gender.male;
import static org.junit.Assert.*;


public class TestMe
{
  private final List<Person> data = Arrays.asList(
    new Person( "Skipper", 4, male, false ),
    new Person( "Pokechop", 3, male, false ),
    new Person( "Smokey", 40, male, true ),
    new Person( "Skeeter", 39, female, true ) );

  @Test
  public void scratchTest()
  {
    Query<Person> query0 = Person.query( ( p, q ) -> q
      .orderBy( p.age ) );
    System.out.println( query0.run( data ) );
    System.out.println( query0 );

    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age >= 39 && p.gender != male )
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertFalse( iterator.hasNext() );
    System.out.println( results );
    System.out.println( query );

    Query<Person> query2 = Person.query( ( p, q ) -> q
      .where( !p.deceased )
      .orderBy( p.age ) );
    System.out.println( query2.run( data ) );
    System.out.println( query2 );
  }

  @Test
  public void simpleFunctionCallTest()
  {
    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age >= 0 && p.name.contains( "S" ) ) // using contains() method
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Skipper", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Smokey", iterator.next().name );
    assertFalse( iterator.hasNext() );
    System.out.println( results );
    System.out.println( query );
  }

  @Test
  public void complexFunctionCallTest()
  {
    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age >= 0 && p.name.toLowerCase().contains( "s" ) ) // using toLowerCase() with contains()
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Skipper", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Smokey", iterator.next().name );
    assertFalse( iterator.hasNext() );
    System.out.println( results );
    System.out.println( query );
  }

  @Test
  public void complexFunctionCallTest2()
  {
    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age - 10 >= 0 && p.name.toLowerCase().contains( "s" ) ) // using toLowerCase() with contains()
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Smokey", iterator.next().name );
    assertFalse( iterator.hasNext() );
    System.out.println( results );
    System.out.println( query );
  }

  @Test
  public void arithmeticTest()
  {
    Numbers nums = new Numbers( (byte)1, (short)1, 1, 1, 1.1f, 1.2, 'a' );
    List<Numbers> numData = Collections.singletonList( nums );
    Query<Numbers> query = Numbers.query( (p, q) -> q.where( p._byte + 2 == 3 ) );
    List<Numbers> results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._short + 2 == 3 ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._int + 2 == 3 ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._float + 2.9f == 4.0f ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );
    query = Numbers.query( (p, q) -> q.where( p._float + 2.9f == 4.0d ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );
    query = Numbers.query( (p, q) -> q.where( p._float + 2.9f == 4 ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );
    query = Numbers.query( (p, q) -> q.where( p._float + 2.9f == 4L ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._double + 2.9d == 4.1d ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );


    query = Numbers.query( (p, q) -> q.where( p._byte + 2 == 3f ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._byte + 2 >= 3f ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._byte + 2 <= 3f ) );
    results = query.run( numData );
    assertEquals( 1, results.size() );

    query = Numbers.query( (p, q) -> q.where( p._byte + 2 < 3f ) );
    results = query.run( numData );
    assertTrue( results.isEmpty() );
  }

  static class Numbers extends Entity
  {
    @val byte _byte;
    @val short _short;
    @val int _int;
    @val long _long;
    @val float _float;
    @val double _double;
    @val char _char;

    public Numbers( byte aByte, short aShort, int anInt, long aLong, float aFloat, double aDouble, char aChar )
    {
      _byte = aByte;
      _short = aShort;
      _int = anInt;
      _long = aLong;
      _float = aFloat;
      _double = aDouble;
      _char = aChar;
    }
  }
}
