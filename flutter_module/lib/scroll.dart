import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

import 'struct/slot.dart';

final List<Color> colorPiker = [
  Colors.red,
  Colors.green,
  Colors.blue,
  Colors.yellow,
];

class CustomScrollViewExampleApp extends StatelessWidget {
  CustomScrollViewExampleApp({super.key});

  final int totalLength = 200;

  final List<Slot> slot = [
    Slot(),
    Slot(),
    Slot(),
    Slot(),
    Slot(),
    Slot(),
    Slot(),
    Slot(),
  ];

  @override
  Widget build(BuildContext context) {
    for (int i = 0; i < totalLength; i++) {
      int slotIndex = minSlot(slot);
      Slot slotOne = slot[slotIndex];
      slotOne.slotItemList.add(SlotItem(i, 100 + i % 4 * 20.0));
      slotOne.totalHeight = slotOne.totalHeight + 100 + i % 4 * 20.0;
    }
    return MaterialApp();
  }
}

class CustomScrollViewWrap extends StatelessWidget {
  const CustomScrollViewWrap({
    super.key,
    required this.slots,
    required this.builder,
    required this.totalLength,
  });

  final SlotGroup slots;

  final Widget? Function(BuildContext, int) builder;

  final int totalLength;

  @override
  Widget build(BuildContext context) {
    const Key centerKey = ValueKey<String>('bottom-sliver-list');

    return Scaffold(
      body: CustomScrollView(
        center: centerKey,
        slivers: <Widget>[
          SliverWaterFall(
            slots,
            key: centerKey,
            delegate: SliverChildBuilderDelegate(
              builder,
              childCount: totalLength,
            ),
          ),
        ],
      ),
    );
  }
}

class SliverWaterFall extends SliverMultiBoxAdaptorWidget {
  const SliverWaterFall(this.slots, {super.key, required super.delegate});

  final SlotGroup slots;

  @override
  RenderSliverMultiBoxAdaptor createRenderObject(BuildContext context) {
    final SliverMultiBoxAdaptorElement element =
        context as SliverMultiBoxAdaptorElement;
    return RenderSliverWaterFall(slots, childManager: element);
  }
}

class _RenderSliverWaterFallParentData extends SliverMultiBoxAdaptorParentData {
  double? scrollOffset;
  double? crossOffSet;
}

class RenderSliverWaterFall extends RenderSliverMultiBoxAdaptor {
  RenderSliverWaterFall(this.slots, {required super.childManager});

  final SlotGroup slots;

  @override
  void setupParentData(covariant RenderObject child) {
    if (child.parentData is! _RenderSliverWaterFallParentData) {
      child.parentData = _RenderSliverWaterFallParentData();
      (child.parentData as _RenderSliverWaterFallParentData).crossOffSet = 0;
    }
  }

  @override
  double childCrossAxisPosition(covariant RenderObject child) {
    return (child.parentData as _RenderSliverWaterFallParentData).crossOffSet!;
  }

  final double padding = 0;

  @override
  void performLayout() {
    log("enter performLayout()");
    List<double> slotHeight = List.generate(slots.slots.length, (_) => 0);
    double minSlotHeight() {
      double min = slotHeight[0];
      for (int i = 0; i < slotHeight.length; i++) {
        if (slotHeight[i] < min) {
          min = slotHeight[i];
        }
      }
      return min;
    }

    final SliverConstraints constraints = this.constraints;
    childManager.didStartLayout();
    childManager.setDidUnderflow(false);

    final double scrollOffset = constraints.scrollOffset + padding;
    log(
      "scrollOffset:$scrollOffset, constraints.scrollOffset:${constraints.scrollOffset}, constraints.cacheOrigin:${constraints.cacheOrigin}",
    );
    assert(scrollOffset >= 0.0);
    // final double remainingExtent = constraints.remainingCacheExtent;
    final double remainingExtent = constraints.remainingPaintExtent;
    assert(remainingExtent >= 0.0);
    final double targetEndScrollOffset =
        constraints.scrollOffset + remainingExtent - padding;
    final BoxConstraints tmpConstraints = constraints.asBoxConstraints();
    final BoxConstraints childConstraints = BoxConstraints(
      maxHeight: tmpConstraints.maxHeight,
      minHeight: tmpConstraints.minHeight,
      maxWidth: tmpConstraints.maxWidth,
      minWidth: tmpConstraints.minWidth / slots.slots.length,
    );
    // int leadingGarbage = 0;
    // int trailingGarbage = 0;

    _RenderSliverWaterFallParentData getRenderSliverWaterFallParentData(
      RenderBox renderBox,
    ) {
      return renderBox.parentData as _RenderSliverWaterFallParentData;
    }

    int? firstIndex;
    bool scrollDown = false;
    if (firstChild == null) {
      firstIndex = 0;
      scrollDown = true;
    } else {
      _RenderSliverWaterFallParentData previousFirstData =
          getRenderSliverWaterFallParentData(firstChild!);
      if (scrollOffset > previousFirstData.scrollOffset!) {
        // scroll down
        scrollDown = true;
        int findFirstIndex(int previusFirstIndex) {
          int startIndex = previusFirstIndex > 0
              ? previusFirstIndex - 1
              : previusFirstIndex;
          int totalLength = slots.slotItemList.length;
          for (int i = startIndex; i < totalLength; i++) {
            SlotItem slotItem = slots.slotItemList[i];
            if (slotItem.itemHeight + slotItem.scrollOffset > scrollOffset) {
              return i;
            }
          }
          return totalLength - 1;
        }

        firstIndex = findFirstIndex(
          (firstChild!.parentData! as _RenderSliverWaterFallParentData).index!,
        );
      } else {
        // scroll up
        scrollDown = false;
      }
    }

    _RenderSliverWaterFallParentData childParentData;

    if (firstChild == null) {
      addInitialChild();
      firstChild!.layout(childConstraints, parentUsesSize: true);
      childParentData = getRenderSliverWaterFallParentData(firstChild!);
      SlotItem slotItem = slots.slotItemList[childParentData.index!];
      childParentData.layoutOffset = slotItem.scrollOffset;
      childParentData.crossOffSet =
          slotItem.slotIndex * tmpConstraints.minWidth / slots.slots.length;
    } else if (!scrollDown) {
      List<int> calShouldInsertSlotIndexs(int firstIndex) {
        List<int> columnChecker = List.generate(
          slots.slots.length,
          (_) => -1,
          growable: false,
        );

        int currFirstIndex = firstIndex;
        columnChecker[slots.slotItemList[currFirstIndex].slotIndex] =
            currFirstIndex;

        int i = currFirstIndex + 1;
        while (true) {
          SlotItem slotItem = slots.slotItemList[i];
          if (columnChecker[slotItem.slotIndex] == -1) {
            columnChecker[slotItem.slotIndex] = i;
          }
          if (!columnChecker.contains(-1)) {
            break;
          }
          i++;
        }
        return columnChecker
            .where((checker) {
              return slots.slotItemList[checker].scrollOffset > scrollOffset;
            })
            .map((checker) => slots.slotItemList[checker].slotIndex)
            .toList();
      }

      firstIndex = getRenderSliverWaterFallParentData(firstChild!).index;
      List<int> shouldInsertSlotIndexs = calShouldInsertSlotIndexs(firstIndex!);
      while (shouldInsertSlotIndexs.isNotEmpty) {
        RenderBox? child = insertAndLayoutLeadingChild(
          childConstraints,
          parentUsesSize: true,
        );
        if (child == null) {
          break;
        }
        childParentData = child.parentData! as _RenderSliverWaterFallParentData;
        SlotItem slotItem = slots.slotItemList[childParentData.index!];
        childParentData.layoutOffset = slotItem.scrollOffset;
        childParentData.crossOffSet =
            slotItem.slotIndex * tmpConstraints.minWidth / slots.slots.length;
        if (slotItem.scrollOffset < scrollOffset &&
            shouldInsertSlotIndexs.contains(slotItem.slotIndex)) {
          shouldInsertSlotIndexs.remove(slotItem.slotIndex);
        }
      }
      firstIndex = getRenderSliverWaterFallParentData(firstChild!).index;
    }

    RenderBox? child;
    RenderBox? lastChild = firstChild;

    while (true) {
      child = childAfter(lastChild!);
      if (child == null) {
        child = insertAndLayoutChild(
          childConstraints,
          after: lastChild,
          parentUsesSize: true,
        );
        if (child == null) {
          break;
        }
      } else {
        child.layout(childConstraints, parentUsesSize: true);
      }
      childParentData = child.parentData! as _RenderSliverWaterFallParentData;
      SlotItem slotItem = slots.slotItemList[childParentData.index!];
      childParentData.layoutOffset = slotItem.scrollOffset;
      childParentData.crossOffSet =
          slotItem.slotIndex * tmpConstraints.minWidth / slots.slots.length;
      slotHeight[slotItem.slotIndex] =
          slotItem.scrollOffset + slotItem.itemHeight;
      lastChild = child;
      if (minSlotHeight() > targetEndScrollOffset) {
        break;
      }
    }
    int trailingGarbage = calculateTrailingGarbage(
      lastIndex:
          (lastChild.parentData as _RenderSliverWaterFallParentData).index!,
    );
    int leadingGarbage = calculateLeadingGarbage(firstIndex: firstIndex!);
    collectGarbage(leadingGarbage, trailingGarbage);

    (firstChild!.parentData! as _RenderSliverWaterFallParentData).scrollOffset =
        scrollOffset;

    double estimatedMaxScrollOffset = slots.totalHeight();
    final double paintExtent = constraints.remainingPaintExtent;
    log("estimatedMaxScrollOffset:$estimatedMaxScrollOffset");
    geometry = SliverGeometry(
      scrollExtent: estimatedMaxScrollOffset,
      paintExtent: paintExtent,
      cacheExtent: paintExtent,
      maxPaintExtent: estimatedMaxScrollOffset,
    );

    childManager.setDidUnderflow(true);
    childManager.didFinishLayout();
  }

  @override
  void paint(PaintingContext context, Offset offset) {
    super.paint(context, offset);
    Offset p1 = Offset(offset.dx, offset.dy + padding);
    Offset p2 = Offset(
      offset.dx + constraints.crossAxisExtent,
      offset.dy + padding,
    );
    context.canvas.drawLine(
      p1,
      p2,
      Paint()
        ..color = Colors.red
        ..strokeWidth = 2,
    );

    Offset p3 = Offset(
      offset.dx,
      offset.dy + constraints.remainingPaintExtent - padding,
    );
    Offset p4 = Offset(
      offset.dx + constraints.crossAxisExtent,
      offset.dy + constraints.remainingPaintExtent - padding,
    );
    context.canvas.drawLine(
      p3,
      p4,
      Paint()
        ..color = Colors.red
        ..strokeWidth = 2,
    );
  }
}
