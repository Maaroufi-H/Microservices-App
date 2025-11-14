package net.maaroufi.orderservice.feign;


import java.util.List;

class Scratch {
   public interface A {
       String print();
   }

   public static class B implements A {
       @Override
       public String print() {
           return "B";
       }

       public String printB() {
           return "Print B";
       }
   }

   public static class C implements A {
       @Override
       public String print() {
           return "C";
       }
   }
   public static void methodWithObject(A object) {
       System.out.println("Calling fObject with: " + object.print());
   }

   public static void methodWithList(List<A> aList) {
       System.out.println("Calling fList with " + aList.size() + " elements:");
       for (A t : aList) {
           System.out.println(t.print());
       }
   }

   public static void main(String[] args) {
       List<B> bList = List.of(new B(), new B());
       for (B b : bList) {
           b.printB();
       }
       C c = new C();
       // Modifica da qui
      
       
	    methodWithObject(c);
		
		//methodWithList(bList);
       
	   // Chiamare methodWithObject con c
       // Chiamare methodWithList con bList
       // Modifica fino a qui
       for (B b : bList) {
           b.printB();
       }
   }
}

