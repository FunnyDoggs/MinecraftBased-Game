package com.funnydoggs;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Integer> blocks;
    private int selectedIndex;

    public Inventory() {
        blocks = new ArrayList<>();
        blocks.add(1); // Example block types
        blocks.add(2);
        blocks.add(3);
        selectedIndex = 0;
    }

    public List<Integer> getBlocks() {
        return blocks;
    }

    public int getSelectedBlock() {
        return blocks.get(selectedIndex);
    }

    public void selectNextBlock() {
        selectedIndex = (selectedIndex + 1) % blocks.size();
    }

    public void selectPreviousBlock() {
        selectedIndex = (selectedIndex - 1 + blocks.size()) % blocks.size();
    }
}
