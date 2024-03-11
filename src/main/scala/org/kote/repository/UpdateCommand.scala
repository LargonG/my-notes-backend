package org.kote.repository

trait UpdateCommand

object UpdateCommand {

  /** Позволяет писать [[UpdatableRepository.update repository.update(obj, cmd)]], вместо того,
    * чтобы каждый раз добавлять обёртку List
    * @param cmd
    *   команда изменения
    * @tparam T
    *   класс изменений конкретного объекта
    * @return
    *   Оборачивает cmd в List
    */
  implicit def commandToList[T <: UpdateCommand](cmd: T): List[T] = List(cmd)
}
