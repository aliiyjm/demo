package com.example.demo.datastructure;

import com.example.demo.model.HistorialOperacion;

public class Stack {
    private StackNode top;


    private static class StackNode {
        HistorialOperacion data;
        StackNode next;
        StackNode(HistorialOperacion data) { this.data = data; }
    }

    //Agregar una operacion al tope
    public void push(HistorialOperacion data) {
        
        StackNode newNode = new StackNode(data);
        newNode.next = top;
        top = newNode;
    }

    //Quita la ultima operacion realizada
    public HistorialOperacion pop() {

        if (isEmpty()) return null;
        HistorialOperacion data = top.data;
        top = top.next;
        return data;
    }

    //Ver si la pila esta vacia
    public boolean isEmpty() { 

        return top == null; 
    }
}
