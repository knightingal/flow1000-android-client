class SlotItem {
  final int index;
  late double scrollOffset;
  final double itemHeight;
  late int slotIndex;
  late int indexInSlot;

  SlotItem(this.index, this.itemHeight);
}

class Slot {
  final List<SlotItem> slotItemList = [];
  double totalHeight = 0;

  SlotItem itemByIndex(int index) {
    return slotItemList.firstWhere((item) {
      return item.index == index;
    });
  }

  bool existByIndex(int index) {
    return slotItemList.any((item) => item.index == index);
  }
}

class SlotGroup {
  factory SlotGroup.fromCount(int slotCount) {
    return SlotGroup(List.generate(slotCount, (_) => Slot(), growable: false));
  }

  final List<SlotItem> slotItemList = [];
  final List<Slot> slots;

  SlotGroup(this.slots);

  void insertSlotItem(SlotItem slotItem) {
    int slotIndex = minSlot();
    Slot slotOne = slots[slotIndex];

    slotItem.slotIndex = slotIndex;
    slotItem.indexInSlot = slotItemList.length;
    slotItem.scrollOffset = slotOne.totalHeight;

    slotOne.slotItemList.add(slotItem);
    slotOne.totalHeight = slotOne.totalHeight + slotItem.itemHeight;
    slotItemList.add(slotItem);
  }

  double totalHeight() {
    double max = 0;
    for (int i = 0; i < slots.length; i++) {
      if (slots[i].totalHeight > max) {
        max = slots[i].totalHeight;
      }
    }
    return max;
  }

  int minSlot() {
    double min = slots[0].totalHeight;
    int index = 0;
    for (int i = 0; i < slots.length; i++) {
      if (slots[i].totalHeight < min) {
        min = slots[i].totalHeight;
        index = i;
      }
    }
    return index;
  }
}

int minSlot(List<Slot> slot) {
  double min = slot[0].totalHeight;
  int index = 0;
  for (int i = 0; i < slot.length; i++) {
    if (slot[i].totalHeight < min) {
      min = slot[i].totalHeight;
      index = i;
    }
  }
  return index;
}
