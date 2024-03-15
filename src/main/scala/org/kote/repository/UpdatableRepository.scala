package org.kote.repository

import cats.data.OptionT

trait UpdatableRepository[F[_], T, ID, CMD] extends Repository[F, T, ID] {

  /** Обновляет значения объекта, если он существует, и возвращает обновлённый. Использование этого
    * метода предпочтительно в тех случаях, когда требуется сделать много изменений, чтобы не
    * обратиться к репозиторию O(1) раз
    * @param id
    *   объекта
    * @param cmds
    *   команды установки новых значений
    * @return
    *   объект после изменений, если он существовал
    */
  def update(id: ID, cmds: CMD*): OptionT[F, T]
}
