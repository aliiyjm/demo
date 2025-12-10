package com.example.demo.datastructure;

import com.example.demo.model.HistorialOperacion;

public class Stack {
    private StackNode top;

    private static class StackNode {
        HistorialOperacion data;
        StackNode next;
        StackNode(HistorialOperacion data) { this.data = data; }
    }

    public void push(HistorialOperacion data) {
        StackNode newNode = new StackNode(data);
        newNode.next = top;
        top = newNode;
    }

    public HistorialOperacion pop() {
        if (isEmpty()) return null;
        HistorialOperacion data = top.data;
        top = top.next;
        return data;
    }

    public boolean isEmpty() { return top == null; }
}
