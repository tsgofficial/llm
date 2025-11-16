# data_struct_and_algo_lab1
{
range 1,1,2,3,5
use B
range 1,2,2,4,4,4
print
union
intersect
toArray
}

{
Enter choice: 1
Enter index: 0
Enter value: 5
Inserted.

Enter choice: 1
Enter index: 1
Enter value: 8
Inserted.

Enter choice: 1
Enter index: 2
Enter value: 3
Inserted.

Enter choice: 9
List: [5, 8, 3]

Enter choice: 3
Max: 8

Enter choice: 4
Min: 3

Enter choice: 5
Sum: 16

Enter choice: 6
Average: 5.333333333333333

Enter choice: 7
Odd values removed.
Enter choice: 9
List: [8]

Enter choice: 0
Exiting.
}

# run 1
cmd /c "javac -d out -sourcepath src\main\eprogs;src\main\java src\main\eprogs\dataStructures\LinearList.java src\main\eprogs\dataStructures\ArrayLinearListIterator.java src\main\eprogs\dataStructures\ArrayLinearList.java src\main\eprogs\utilities\ChangeArrayLength.java src\main\java\MyArrayLinearList.java"
java -cp out MyArrayLinearList

# run 2
mkdir out -Force | Out-Null
javac -d out -sourcepath "src\main\eprogs;src\main\java" "src\main\java\dataStructures\MyChain.java"
java -cp out dataStructures.MyChain

# run 3
mkdir out -Force | Out-Null
javac -d out -sourcepath "src\main\eprogs;src\main\java" "src\main\java\dataStructures\MyStack.java" 
java -cp out dataStructures.MyStack

# run 4
mkdir out -Force | Out-Null
javac -d out -sourcepath "src\main\eprogs;src\main\java" "src\main\java\dataStructures\MyHashChains.java"
java -cp out dataStructures.MyHashChains

# run 5
mkdir out -Force | Out-Null
javac -d out -sourcepath "src\main\eprogs;src\main\java" "src\main\java\dataStructures\Expression.java"
java -cp out dataStructures.Expression

# values 5
1,2,3,4
rule: 
Prefix-ийн дүрэм: operator  leftSubtree  rightSubtree
Postfix-ийн дүрэм: operand operand operator

# Biydaalt 1
cmd /c "javac -d out -sourcepath src\main\eprogs;src\main\java src\main\eprogs\dataStructures\LinearList.java src\main\eprogs\dataStructures\ArrayLinearListIterator.java src\main\eprogs\dataStructures\ChainNode.java src\main\eprogs\dataStructures\ArrayLinearList.java src\main\eprogs\dataStructures\Chain.java src\main\eprogs\utilities\ChangeArrayLength.java src\main\java\dataStructures\biyDaalt.java && java -cp out dataStructures.biyDaalt data\Subjects.txt data\Professions.txt data\Exams.txt"

java -cp out dataStructures.biyDaalt data\Subjects.txt data\Professions.txt data\Exams.txt


java -cp out dataStructures.MainApp src\main\data\Subjects.txt src\main\data\Professions.txt src\main\data\Exams.txt

# Biydaalt 2
javac -d out -sourcepath "src\main\java;src\main\eprogs" `
  src\main\eprogs\dataStructures\Stack.java `
  src\main\java\dataStructures\MyStack.java `
  src\main\java\dataStructures\CarParking.java

java -cp out dataStructures.CarParking "src\main\data\cars.txt"

# Biydaalt 3
mkdir out -Force | Out-Null
javac -d out -sourcepath "src\main\eprogs;src\main\java" "src\main\java\dataStructures\GraphDijkstra.java"
java -cp out dataStructures.GraphDijkstra
